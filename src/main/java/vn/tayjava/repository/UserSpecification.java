package vn.tayjava.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import vn.tayjava.model.Role;
import vn.tayjava.model.User;

public class UserSpecification {
    public static Specification<User> hasRole(String roleName) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roles = root.join("roles", JoinType.LEFT);
            return criteriaBuilder.equal(roles.get("name"), roleName);
        };
    }

    public static Specification<User> sortByRole(String direction) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roles = root.join("roles", JoinType.LEFT);
            query.orderBy(direction.equalsIgnoreCase("ASC") ?
                    criteriaBuilder.asc(roles.get("name")) :
                    criteriaBuilder.desc(roles.get("name")));
            return criteriaBuilder.conjunction();
        };
    }
}
