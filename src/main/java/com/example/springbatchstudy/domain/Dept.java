package com.example.springbatchstudy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Dept {

    @Id
    @Column(name = "dept_no")
    Integer deptNo;

    String dName;
    String loc;

}
