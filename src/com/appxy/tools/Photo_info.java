package com.appxy.tools;

public class Photo_info{
	
	String name;
	String time;
	int image_num;
	String image_name;
	boolean check;
	public Photo_info(String name, String time, int image_num, String image_name, boolean check){
		this.name = name;
		this.time = time;
		this.image_num = image_num;
		this.image_name = image_name;
		this.check = check;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getImage_num() {
		return image_num;
	}
	public void setImage_num(int image_num) {
		this.image_num = image_num;
	}
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	

}
