package it.unifi;

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {

        ArrayList<Conteiner> allConteiners = new ArrayList<>();
        ArrayList<PToken> p = new ArrayList<>();
        InputStream is = new FileInputStream("file.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Integer line;
        String l;
        int max;
        int min;

        line = Integer.parseInt(br.readLine());

        for (int i = 0; i < line; i++) {
            allConteiners.add(new Conteiner(40, i));
        }

        Conteiner.conteiners = allConteiners;

        allConteiners.get(0).add(new Object());
        allConteiners.get(1).add(new Object());

        do {
            ArrayList<Conteiner> conteinersIn = new ArrayList<>();
            ArrayList<Conteiner> conteinersOut = new ArrayList<>();
            do {
                l = br.readLine();
                if (l.equals("-")) {
                    break;
                }
                conteinersIn.add(allConteiners.get((Integer.parseInt(l))));
            } while (true);


            do {
                l = br.readLine();
                if (l.equals("-")) {
                    break;
                }

                conteinersOut.add(allConteiners.get((Integer.parseInt(l))));

            } while (true);

            min = Integer.parseInt(br.readLine());
            max = Integer.parseInt(br.readLine());

            l = br.readLine();

            p.add(new PToken(conteinersIn, conteinersOut, min, max));

            p.get(p.size() - 1).setName("T" + (p.size() - 1));
            p.get(p.size() - 1).start();

        } while (l != null);

        is.close();

        Thread.sleep(30000);

        for (PToken x : p) {
            x.interrupt();
        }

    }
}


class Conteiner {
    private ArrayList<Object> p = new ArrayList<>();
    public static ArrayList<Conteiner> conteiners;
    private int max;
    public int id;

    Conteiner(int max, int id) {
        this.max = max;
        this.id = id;
    }

    synchronized void add(Object token) throws InterruptedException {
        while (p.size() == max) {
            wait();
        }
        p.add(token);
        System.out.println("Added in: " + this.id);
        print();
        notifyAll();
    }

    synchronized Object remove(ArrayList<Conteiner> conteinersIn) throws InterruptedException {
        for (int i = 0; i < conteinersIn.size(); i++) {
            if (conteinersIn.get(i).getSize() == 0) {
                wait();
                i = -1;
            }
        }

        Object r = p.remove(0);
        System.out.println("Removed from:" + this.id);
        print();
        notifyAll();
        return r;
    }

    synchronized static void print() {
        for (int i = 0; i < conteiners.size(); i++) {
            System.out.println(conteiners.get(i).id + ", " + conteiners.get(i).getSize() + ", " + Thread.currentThread().getName());
        }
    }

    int getSize() {
        return p.size();
    }
}

class PToken extends Thread {
    private ArrayList<Conteiner> conteinersIn;
    private ArrayList<Conteiner> conteinersOut;
    private int min;
    private int max;

    PToken(ArrayList<Conteiner> conteinersIn, ArrayList<Conteiner> conteinersOut, int min, int max) {
        this.conteinersIn = conteinersIn;
        this.conteinersOut = conteinersOut;
        this.min = min;
        this.max = max;
    }


    @Override
    public void run() {
        try {
            while (true) {
                for (int i = 0; i < conteinersIn.size(); i++) {
                    Object r = conteinersIn.get(i).remove(conteinersIn);
                    Thread.sleep((int) ((Math.random() * (max - min)) + min));
                    for (int j = 0; j < conteinersOut.size(); j++) {
                        System.out.println("From " + conteinersIn.get(i).id + " to " + conteinersOut.get(j).id + ", " + Thread.currentThread().getName());
                        conteinersOut.get(j).add(r);
                    }
                }

            }
        } catch (InterruptedException e) {

        }
    }
}