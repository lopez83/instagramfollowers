package com.olopez.instagramfollowers.model;

public class Counter {

	private int icon;
	private int title;
	private int counter;

	public Counter(int title, int counter) {
		this(-1, title, counter);
	}

	public Counter(int icon, int title, int counter) {
		super();
		this.icon = icon;
		this.title = title;
		this.counter = counter;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}