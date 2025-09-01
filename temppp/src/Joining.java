public class Joining {
    public static void main(String[] args) {
        Thread[] threads = new Thread[8];
        Thread last = null;
        for (int i = 0; i <8; i++) {
            threads[i] = new ThreadA(last);
            last = threads[i];

        }
    }
}

class ThreadA extends Thread {
    Thread last;

    public ThreadA(Thread last) {
        this.last=last;
    }


    @Override
    public void run() {
        try {
            if (last != null) {
                last.join();
            }
        } catch (InterruptedException e) {
           throw new RuntimeException(e);
        }
        System.out.println("hi");
    }
}
