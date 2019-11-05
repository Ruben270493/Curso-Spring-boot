package com.bolsadeideas.springboot.app.util.paginator;

public class PageItem {

	private int number;
	private boolean isCurrentPage;
	
	public PageItem(int number, boolean isCurrentPage) {
		this.number = number;
		this.isCurrentPage = isCurrentPage;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isCurrentPage() {
		return isCurrentPage;
	}

	public void setCurrentPage(boolean isCurrentPage) {
		this.isCurrentPage = isCurrentPage;
	}
	
}
