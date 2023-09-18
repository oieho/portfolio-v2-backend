package com.oieho.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recoverpassword")
@ToString
public class RecoverPassword {

	@Id
    @Column(name = "resetToken", length = 256)
    @NotNull
    @Size(max = 256)
    private String resetToken;
}
