package com.projet6.PayMyBuddy.dto;

public class SocialNetworkUserDto {
	
	private String email_id;
    private String name;
    private String password;
    private String role;
    
	public SocialNetworkUserDto() {
		super();
	}
	
	public SocialNetworkUserDto(String email_id, String name, String password,
			String role) {
		super();
		this.email_id = email_id;
		this.name = name;
		this.password = password;
		this.role = role;
	}
	@Override
	public String toString() {
		return "SocialNetworkUserDto [email_id=" + email_id + ", name=" + name
				+ ", password=" + password + ", role=" + role + "]";
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
    

}
