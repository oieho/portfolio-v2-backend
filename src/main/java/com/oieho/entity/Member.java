package com.oieho.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oieho.oauth.entity.ProviderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(value="hibernateLazyInitializer")
@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@RequiredArgsConstructor
public class Member extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = true)
	private Long userNo;
	
	@NotBlank
	@Column(unique=true, length = 50, nullable = false)
	private String userId; 
	
	@NotBlank
	@Column(length = 200, nullable = false)
	private String userPw;
	
	@NotBlank
	@Column(unique=true, length = 100, nullable = false)
	private String userName;
	
	@NotBlank
	@Column(unique=true, length = 100, nullable = false)
	private String userEmail;
	
	@Column(name = "reg_date")
    protected LocalDateTime regDate;

    @PrePersist
	public void onCreate() {
        regDate = LocalDateTime.now();
    }
	
	@Column(name = "ROLE_TYPE", length = 10)
    @Enumerated(EnumType.STRING)
    @NotNull
	private @NotNull RoleType roleType;
	
	@Column(name = "PROVIDER_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ProviderType providerType;
	
	@Column(length = 15, nullable = true)
	private ArrayList<GrantedAuthority> authorities;

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<WorkComment> workComment;
	
	public Collection<? extends GrantedAuthority> getRole() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setUserNo(long userNo) {
		this.userNo = userNo;
		
	}

	public Member(@NotNull @Size(max = 64) String userId, @NotNull @Size(max = 100) String userName, @NotNull @Size(max = 100) String email, @NotNull RoleType roleType, @NotNull ProviderType providerType,
			LocalDateTime regDate, LocalDateTime updDate) {
		 this.userId = userId;
		 this.userPw = "No Password";
	        this.userName = userName;
	        this.userEmail = email != null ? email : "No Email";
	        this.roleType = roleType != null ? roleType : RoleType.USER ;
	        this.providerType = providerType;
	        this.regDate = regDate;
	        this.updDate = updDate;
	}
} 