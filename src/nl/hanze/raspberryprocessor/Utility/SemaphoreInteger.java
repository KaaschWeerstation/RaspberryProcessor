package nl.hanze.raspberryprocessor.Utility;

public class SemaphoreInteger {

    private int value = 0;

    public SemaphoreInteger(int integer) {
        this.value = integer;
    }

    public synchronized void increment() {
        this.value++;
        this.notify();
    }

    public synchronized void decrement() throws  InterruptedException {
        while (value < 0) {
            wait(0);
        }
        value--;
        this.notify();
    }

    public synchronized int get() {
        return value;
    }




}
