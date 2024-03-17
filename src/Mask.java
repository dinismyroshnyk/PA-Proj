public class Mask implements Runnable {
    private boolean end = false;

    public Mask(String prompt) {
        System.out.print(prompt);
        System.out.print(" ");
    }

    public void run() {
        while (!end) {
            System.out.print("\b*");
        }
    }

    public synchronized void maskEnd() {
        this.end = true;
    }
}
