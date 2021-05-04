package com.game.service.impl.filters;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
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
                case "banned":
                    Boolean banned;
                    try {
                        banned = Boolean.parseBoolean(entry.getValue());
                    } catch (Exception e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.BOOLEAN,
                            Comparison.EQUALS,
                            entry.getKey(),
                            banned));
                    break;
                case "race":
                    Race race;
                    try {
                        race = Race.valueOf(entry.getValue());
                    } catch (IllegalArgumentException | NullPointerException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.STRING,
                            Comparison.EQUALS,
                            entry.getKey(),
                            race));
                    break;
                case "profession" :
                    Profession profession;
                    try {
                        profession = Profession.valueOf(entry.getValue());
                    } catch (IllegalArgumentException | NullPointerException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.STRING,
                            Comparison.EQUALS,
                            entry.getKey(),
                            profession));
                    break;
                case "after" :
                    Date after;
                    try {
                        after = new Date(Long.parseLong(entry.getValue()));
                    } catch (NumberFormatException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.DATE,
                            Comparison.MORE,
                            "birthday",
                            after));
                    break;
                case "before" :
                    Date before;
                    try {
                        before = new Date(Long.parseLong(entry.getValue()));
                    } catch (NumberFormatException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.DATE,
                            Comparison.LESS,
                            "birthday",
                            before));
                    break;
                case "minLevel":
                    Integer minNumber;
                    try {
                        minNumber = Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.INTEGER,
                            Comparison.MORE,
                            "level",
                            minNumber));
                    break;
                case "minExperience" :
                    try {
                        minNumber = Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.INTEGER,
                            Comparison.MORE,
                            "experience",
                            minNumber));
                    break;
                case "maxLevel":
                    Integer maxNumber;
                    try {
                        maxNumber = Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.INTEGER,
                            Comparison.LESS,
                            "level",
                            maxNumber));
                    break;
                case "maxExperience" :
                    try {
                        maxNumber = Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e){
                        break;
                    }
                    conditions.add(new Condition(
                            TypeValue.INTEGER,
                            Comparison.LESS,
                            "experience",
                            maxNumber));
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
        Number number = 0;
        Date date = null;
        if (condition.getTypeValue() == TypeValue.INTEGER)
            number = (Integer)condition.getValue();
        else if (condition.getTypeValue() == TypeValue.DATE)
            date = (Date)condition.getValue();
        switch (condition.getComparison()){
            case CONTAINS:
                String pattern = String.format("%%%s%%", condition.getValue());
                return criteriaBuilder.like(root.get(condition.getField()), pattern);
            case EQUALS:
                return criteriaBuilder.equal(root.get(condition.getField()), condition.getValue());
            case LESS:
                if (date == null)
                    return criteriaBuilder.lt(root.get(condition.getField()), number);
                else {
                    return criteriaBuilder.lessThan(root.get(condition.getField()), date);
                }
            case MORE:
                if (date == null)
                    return criteriaBuilder.gt(root.get(condition.getField()), number);
                else {
                    return criteriaBuilder.greaterThan(root.get(condition.getField()), date);
                }

        }
        return null;
    }
}
