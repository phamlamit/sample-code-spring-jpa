package vn.tayjava.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key; // firstname, lastName, email
    private String operation; // =, <, >
    private Object values;

}
