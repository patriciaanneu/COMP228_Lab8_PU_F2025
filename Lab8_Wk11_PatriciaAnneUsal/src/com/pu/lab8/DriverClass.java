package com.pu.lab8;

import java.util.Map;
import java.util.Random;

public class DriverClass {

	public static void main(String[] args) throws InterruptedException{
		InventoryMgmt inventory = new InventoryMgmt();
		inventory.addStock("Prod-1", 100);
		inventory.addStock("Prod-2", 75);
		inventory.addStock("Prod-3", 85);
		
		StoppableTask salesTask = new SalesTask(inventory, "Prod-1");
		StoppableTask restockTask = new RestockTask(inventory, "Prod-1");
		StoppableTask auditTask = new AuditTask(inventory);
		
		Thread salesThread = new Thread(salesTask, "Sales-Thread"); //simulate sales
		Thread restockThread = new Thread(restockTask, "Restock-Thread"); //simulates restocking
		Thread auditThread = new Thread(auditTask, "Audit-Thread"); //for monitoring inventory
		
		salesThread.start();
		restockThread.start();
		auditThread.start();
		
		Thread.sleep(1000);
		
		System.out.println("Requesting stop for all threads...");
		salesTask.stop();
		restockTask.stop();
		auditTask.stop();
		
		salesThread.join();
        restockThread.join();
        auditThread.join();
		
		System.out.println("Final inventory: " + inventory.getSnapshot());

	}
	
	interface StoppableTask extends Runnable {
		void stop();
	}
	
	static class SalesTask implements StoppableTask {
		private final InventoryMgmt inventory;
		private final String productId;
		private volatile boolean running = true;
		private final Random rnd = new Random();
		
		SalesTask(InventoryMgmt inventory, String productId) {
			this.inventory = inventory;
			this.productId = productId;
		}
		
		@Override
		public void run() {
			while (running) {
				int qty = rnd.nextInt(5) + 1;
				inventory.removeStock(productId, qty);
				sleepQuiet(20, 50);
			}
		}
		
		@Override
		public void stop() { running = false; }
	}
	
	static class RestockTask implements StoppableTask {
		private final InventoryMgmt inventory;
		private final String productId;
		private volatile boolean running = true;
		private final Random rnd = new Random();
		
		RestockTask(InventoryMgmt inventory, String productId) {
			this.inventory = inventory;
			this.productId = productId;
		}
		
		@Override
        public void run() {
            while (running) {
                int qty = rnd.nextInt(10) + 1;
                inventory.addStock(productId, qty);
                sleepQuiet(30, 60);
            }
        }
		
		@Override
		public void stop() { running = false; }
	}
	
	static class AuditTask implements StoppableTask {
		private final InventoryMgmt inventory;
		private volatile boolean running = true;
		
		AuditTask(InventoryMgmt inventory) { this.inventory = inventory; }
		
		@Override
		public void run() {
			while (running) {
				Map<String, Integer> snap = inventory.getSnapshot();
				System.out.println("[" + Thread.currentThread().getName() + "] Audit: " + snap);
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		
		@Override
		public void stop() { running = false; }
	}
	
	private static void sleepQuiet(int base, int jitter) {
		try {
			Thread.sleep(base + new Random().nextInt(jitter));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				}
	}

}
