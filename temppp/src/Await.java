public class Await {
    public static void await(boolean guard) {
        while (!guard) {
            Thread.yield();
        }
    }

    public static void main(String[] args) {
        Thread a = new MyEvenThread();
        Thread b = new MyOddThread();
        a.start();
        b.start();
    }
}

class Shared {
    public static int amount = 0;
}

class MyEvenThread extends Thread {
    @Override
    public void run(){
        while (true) {
            Await.await(Shared.amount%2 == 0);
            Shared.amount++;
            System.out.println(Shared.amount);
        }
    }

}

class MyOddThread extends Thread {
    @Override
    public void run(){
        while (true) {
            Await.await(Shared.amount%2 == 1);
            Shared.amount++;
            System.out.println(Shared.amount);
        }
    }

}
