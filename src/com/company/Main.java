package com.company;

import java.util.Random;
import java.util.concurrent.*;


import static com.company.Main.EOF;

public class Main
{

    public static final String EOF = "EOF";

    public static void main(String[] args)
    {
	    ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<String>(6);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_PURPLE);
	    MyConsumer consumer = new MyConsumer(buffer, ThreadColor.ANSI_BLUE);
	    MyConsumer secondCon = new MyConsumer(buffer, ThreadColor.ANSI_RED);
        executorService.execute(producer);
        executorService.execute(consumer);
        executorService.execute(secondCon);
        Future<String> future = executorService.submit(new Callable<String>()
        {
            @Override
            public
            String call()
            throws Exception
            {
                System.out.println(ThreadColor.ANSI_BOLD + ThreadColor.ANSI_GREEN + "Callable Object");
                return "this is the callable result";
            }
        });
        try
        {
            System.out.println(future.get());
        }
        catch(InterruptedException | ExecutionException e)
        {
            System.out.println(e.getMessage());
        }
        executorService.shutdown();
    }
}



class MyProducer
implements Runnable
{
    private ArrayBlockingQueue<String> buffer;
    private String color;

    public MyProducer(ArrayBlockingQueue<String> buffer, String color)
    {
        this.buffer = buffer;
        this.color = color;
    }



    @Override
    public
    void run()
    {
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};

        for (String num:nums)
        {

            try
            {
                System.out.println(color + "Adding..." + num);
                buffer.put(num);
                Thread.sleep(random.nextInt(250));
            }
            catch(InterruptedException i)
            {
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(color + "Adding EOF and exiting . . . ");
        try
        {
            buffer.put(EOF);
        }catch(InterruptedException i)
        {
            System.out.println("buffer EOF put failed");
        }

    }
}



class MyConsumer
implements Runnable
{
    private ArrayBlockingQueue<String> buffer;
    private String color;

    public MyConsumer(ArrayBlockingQueue<String> buffer, String color)
    {
        this.buffer = buffer;
        this.color = color;
    }

    @Override
    public
    void run()
    {
        while(true)
        {
            synchronized (buffer)
            {
                try{
                    if (buffer.isEmpty())
                    {
                        continue;
                    }
                    if (buffer.peek().equals(EOF))
                    {
                        System.out.println(color + "Exiting. . .");
                        break;
                    }
                    else
                    {
                        System.out.println(color + "Removed " + buffer.take());
                    }
                }
                catch(InterruptedException i)
                {
                    i.getMessage();
                }
            }


        }
    }
}