public class Mask implements Runnable {
    private boolean end = false;

    public Mask(String prompt) {
        System.out.print(prompt);
        System.out.print(" ");
    }

    public void run() {
        while (!end) {
            System.out.print("\b*");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Error masking password.");
                System.out.println("Exception: " + e);
            }
        }
    }

    public synchronized void maskEnd() {
        this.end = true;
    }
}
