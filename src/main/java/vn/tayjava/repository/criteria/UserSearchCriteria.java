package vn.tayjava.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria implements Consumer<SearchCriteria> {

    private CriteriaBuilder builder;
    private Predicate predicate;
    private Root root;

    @Override
    public void accept(SearchCriteria param) {
        if (param.getOperation().equals(">")) {
            predicate = (Predicate) builder.and((Expression<Boolean>) predicate, builder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValues().toString()));
        } else if (param.getOperation().equals("<")) {
            builder.and((Expression<Boolean>) predicate, builder.lessThanOrEqualTo(root.get(param.getKey()), param.getValues().toString()));
        } else {
            if(root.get(param.getKey()).getJavaType() == String.class){
                builder.and((Expression<Boolean>) predicate, builder.like(root.get(param.getKey()),"%" + param.getValues().toString() + "%"));
            }else {
                builder.and((Expression<Boolean>) predicate, builder.equal(root.get(param.getKey()), param.getValues().toString()));
            }
        }
    }
}
