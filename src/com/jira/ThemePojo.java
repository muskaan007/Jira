package com.jira;

public class ThemePojo {

private int yetToBePickedUp;
private int pickedUpInCurrentSprint;
private int completed;
private int total;


public ThemePojo(int yetToBePickedUp, int pickedUpInCurrentSprint, int completed, int total) {
	super();
	this.yetToBePickedUp = yetToBePickedUp;
	this.pickedUpInCurrentSprint = pickedUpInCurrentSprint;
	this.completed = completed;
	this.total = total;
}


@Override
public String toString() {
	return "ThemePojo [yetToBePickedUp=" + yetToBePickedUp + ", pickedUpInCurrentSprint=" + pickedUpInCurrentSprint
			+ ", completed=" + completed + ", total=" + total + "]";
}


public int getYetToBePickedUp() {
	return yetToBePickedUp;
}


public void setYetToBePickedUp(int yetToBePickedUp) {
	this.yetToBePickedUp = yetToBePickedUp;
}


public int getPickedUpInCurrentSprint() {
	return pickedUpInCurrentSprint;
}


public void setPickedUpInCurrentSprint(int pickedUpInCurrentSprint) {
	this.pickedUpInCurrentSprint = pickedUpInCurrentSprint;
}


public int getCompleted() {
	return completed;
}


public void setCompleted(int completed) {
	this.completed = completed;
}


public int getTotal() {
	return total;
}


public void setTotal(int total) {
	this.total = total;
}


public ThemePojo() {
	super();
}

}
