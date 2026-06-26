package gytis.courier.adapter.out.persistence.person.courier;

import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "couriers")
public class CourierJpaEntity extends PersonJpaEntity {
/*    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskJpaEntity> assignedTasks = new ArrayList<>();*/

    @Column(nullable = false)
    private boolean hasActiveTask;

    protected CourierJpaEntity() {}

    public CourierJpaEntity(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getRole() { return "COURIER"; }

/*
    public List<TaskJpaEntity> getAssignedTasks() { return assignedTasks; }
*/
    public boolean hasActiveTask() { return hasActiveTask; }

/*
    public void setAssignedTasks(List<TaskJpaEntity> assignedTasks) { this.assignedTasks = assignedTasks; }
*/
    public void setHasActiveTask(boolean hasActiveTask) { this.hasActiveTask = hasActiveTask; }
}
