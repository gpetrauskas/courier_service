package gytis.courier.adapter.out.persistence.person.admin;

import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class AdminJpaEntity extends PersonJpaEntity {
/*    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskJpaEntity> createdTasks = new ArrayList<>();*/

    @Override
    public String getRole() { return "ADMIN"; }
/*
    public List<TaskJpaEntity> getCreatedTasks() { return createdTasks; }

    public void setCreatedTasks(List<TaskJpaEntity> createdTasks) { this.createdTasks = createdTasks; }*/
}
