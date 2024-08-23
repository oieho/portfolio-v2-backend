package com.oieho.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oieho.oauth.entity.ProviderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@Column(length = 100, nullable = false)
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