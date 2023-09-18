package com.oieho.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refreshtokens")
public class RefreshToken {
	// toString Override
	@Override
	public String toString() {
		return refreshToken;
	}
    
    @JsonIgnore
    @Id
    @Column(name = "REFRESH_TOKEN_SEQ")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenSeq;

    @Column(name = "userId", length = 64, unique = true)
    @NotNull
    @Size(max = 64)
    private String userId;

    @NotNull
    private Date expirationTime;
    
    @Column(name = "refreshToken", length = 512)
    @NotNull
    @Size(max = 512)
    private String refreshToken;

    public RefreshToken(
            @NotNull @Size(max = 64) String userId,
            @NotNull @Size(max = 512) String refreshToken
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
    
}