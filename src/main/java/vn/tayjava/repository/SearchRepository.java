package vn.tayjava.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.tayjava.dto.response.PageResponse;
import vn.tayjava.model.User;
import vn.tayjava.repository.criteria.SearchCriteria;
import vn.tayjava.repository.criteria.UserSearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> getAllUserSWithSoftByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        //get page user
        StringBuilder sqlQuery = new StringBuilder("select new vn.tayjava.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) from User u where 1=1 ");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" and lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" and lower(u.email) like lower(:email)");
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format("%%%s%%", search));
            selectQuery.setParameter("lastName", String.format("%%%s%%", search));
            selectQuery.setParameter("email", String.format("%%%s%%", search));
        }
        List users = selectQuery.getResultList();

        //count element
        StringBuilder countQuery = new StringBuilder("select count(*) from User u where 1=1 ");
        if (StringUtils.hasLength(search)) {
            countQuery.append(" and lower(u.firstName) like lower(:firstName)");
            countQuery.append(" and lower(u.lastName) like lower(:lastName)");
            countQuery.append(" and lower(u.email) like lower(:email)");
        }

        Query selectCountQuery = entityManager.createQuery(countQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectCountQuery.setParameter("firstName", String.format("%%%s%%", search));
            selectCountQuery.setParameter("lastName", String.format("%%%s%%", search));
            selectCountQuery.setParameter("email", String.format("%%%s%%", search));
        }
        Long totalElement = (Long) selectCountQuery.getSingleResult();

        Page<?> page = new PageImpl<Object>(users, PageRequest.of(pageNo, pageSize), totalElement);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .items(page.stream().toList())
                .totalPage(page.getTotalPages())
                .build();
    }

    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String softBy, String... search) {
        //firstname:T, lastname:T
        List<SearchCriteria> criteriaList = new ArrayList<>();
        if(search != null){
            for (String s : search) {
                //firstName:values
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                   criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2),matcher.group(3)));
                }
            }
        }
        //1. lay ra list user
        List<User> users = getUsers(pageNo, pageSize, criteriaList, softBy);

        //2. lay ra total element
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .items(users)
                .totalPage(0)
                .build();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String softBy) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        //Xu ly cac dieu kien tim kiem
        Predicate predicate = (Predicate) builder.conjunction();
        UserSearchCriteria queryConsumer = new UserSearchCriteria(builder, predicate, root);

        criteriaList.forEach(queryConsumer);
        predicate = queryConsumer.getPredicate();

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
    }
}
