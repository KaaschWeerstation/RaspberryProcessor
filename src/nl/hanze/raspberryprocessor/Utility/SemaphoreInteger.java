package nl.hanze.raspberryprocessor.Utility;

public class SemaphoreInteger {

    private int value;

    public SemaphoreInteger(int integer) {
        this.value = integer;
    }

    public synchronized void increment() {
        this.value++;
        this.notifyAll();
    }

    public synchronized void decrement() throws  InterruptedException {
        while (value < 0) {
            wait(0);
        }
        value--;
        this.notifyAll();
    }

    public synchronized int get() {
        return value;
    }




}
