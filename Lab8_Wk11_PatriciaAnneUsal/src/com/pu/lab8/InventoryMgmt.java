package com.pu.lab8;

import java.util.HashMap;
import java.util.Map;

public class InventoryMgmt {
	private final Map<String, Integer> inventory = new HashMap<>();
	
	public InventoryMgmt() {}

	public synchronized void addStock(String productId, int quantity) {
		if (productId == null || quantity <= 0) return;
		int newQty = inventory.getOrDefault(productId, 0) + quantity;
		inventory.put(productId, newQty);
		System.out.printf("[%s] addStock: +%d to %s -> %d\n", Thread.currentThread().getName(), quantity, productId, newQty);
 	}
	
	public synchronized boolean removeStock(String productId, int quantity) {
		if (productId == null || quantity <= 0) return false;
		int current = inventory.getOrDefault(productId, 0);
		if (current < quantity) {
			System.out.printf("[%s] removeStock FAILED: %s requested=%d available=%d\n", Thread.currentThread().getName(), productId, quantity, current);
			return false;
		}
		int newQty = current - quantity;
		inventory.put(productId, newQty);
		System.out.printf("[%s] removeStock: -%d from %s -> %d\n", Thread.currentThread().getName(), quantity, productId, newQty);
		return true;
	}
	
	public synchronized Map<String, Integer> getSnapshot() {
		return new HashMap<>(inventory);
	}
	
	public synchronized int getStock(String productId) {
		return inventory.getOrDefault(productId, 0);
	}
}
