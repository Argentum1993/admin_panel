package com.game.service.impl.filters;

import com.game.entity.Player;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerFilters implements Specification<Player> {
    private List<Condition> conditions;

    private PlayerFilters(List<Condition> conditions){
        this.conditions = conditions;
    }

    public static PlayerFilters createFiltersFromParams(Map<String, String> params){
        List<Condition> conditions = createConditions(params);
        if (!conditions.isEmpty())
            return new PlayerFilters(conditions);
        return null;
    }

    @Override
    public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = buildPredicates(root, query, criteriaBuilder);

        if (predicates.size() == 1)
            return predicates.get(0);
        else{
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

    private static List<Condition> createConditions(Map<String, String> params){
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "title":
                case "name":
                    conditions.add(new Condition(
                            TypeValue.STRING,
                            Comparison.CONTAINS,
                            entry.getKey(),
                            entry.getValue()));
                    break;
            }
        }
        return conditions;
    }

    private List<Predicate> buildPredicates(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(condition -> predicates.add(createPredicate(condition, root, query, criteriaBuilder)));
        return predicates;
    }

    private Predicate createPredicate(
            Condition condition,
            Root<Player> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder)
    {
        switch (condition.getComparison()){
            case CONTAINS:
                String pattern = String.format("%%%s%%", condition.getValues().get(0));
                return criteriaBuilder.like(root.get(condition.getField()), pattern);
        }
        return null;
    }
}
