package ecs.components;

import ecs.entities.Entity;
import java.util.HashSet;
import java.util.Set;

public class SkillComponent extends Component {

    public static String name = "SkillComponent";

    private Set<Skill> skillSet;

    /**
     * @param entity associated entity
     */
    public SkillComponent(Entity entity) {
        super(entity, name);
        skillSet = new HashSet<>();
    }

    /**
     * Add a skill to this component
     *
     * @param skill to add
     */
    public void addSkill(Skill skill) {
        skillSet.add(skill);
    }

    /**
     * remove a skill from this component
     *
     * @param skill to remove
     */
    public void removeSkill(Skill skill) {
        skillSet.remove(skill);
    }

    /**
     * @return Set with all skills of this component
     */
    public Set<Skill> getSkillSet() {
        return skillSet;
    }
}