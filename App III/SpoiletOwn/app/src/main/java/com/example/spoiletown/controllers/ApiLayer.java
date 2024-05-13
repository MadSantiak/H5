package com.example.spoiletown.controllers;


import android.util.Log;

import com.example.spoiletown.toilet.IToiletService;
import com.example.spoiletown.toilet.Toilet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Response;

public class ApiLayer {

    /**
     * Generalized Comment:
     * getXByID:
     *      Fetches X object from Database using IXService, which in turn uses ApiLayer, sending variable (int) along in order to find the correct dataset.
     * getAllX:
     *      Fetches all X objects from database tabel, returns List of X objects.
     * addX:
     *      Adds a Toilet object and returns the ID of the created object
     * delX:
     *      Deletes a given recordset based on the ID sent along as an argument.
     * updateX:
     *      Updates the X object sent along as an argument.
     */
    public static Toilet getToiletById (int id)
    {
        FutureTask<Toilet> futureTask = new FutureTask<>(new Callable<Toilet>() {
            @Override
            public Toilet call() {
                Toilet p = null;
                IToiletService serv =
                        ServiceBuilder.buildService(IToiletService.class);

                Call<Toilet> req = serv.getToiletById(id);
                try {
                    p = req.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("ApiLayer", "Failed to fetch data.");
                }
                return p;
            }
        });
        Thread t = new Thread(futureTask);
        t.start();
        Toilet Toilet = null;
        try {
            Toilet = futureTask.get();
        } catch (Exception e) {
            Log.d("Thread", e.getMessage());
        }
        return Toilet;
    }
    public static List<Toilet> getAllToilet() {
        FutureTask<List<Toilet>> futureTask = new FutureTask<>(new Callable<List<Toilet>>() {
            @Override
            public List<Toilet> call() throws Exception {
                List<Toilet> people = null;
                IToiletService serv = ServiceBuilder.buildService(IToiletService.class);
                Call<List<Toilet>> request = serv.getAllToilet();
                try {
                    people = request.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return people;
            }
        });
        Thread t = new Thread(futureTask);
        t.start();
        List<Toilet> people = null;
        try {
            people = futureTask.get();
        } catch (Exception e) {
        }
        return people;
    }

    public static Integer addToilet(Toilet Toilet)
    {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer i = null;
                IToiletService serv = ServiceBuilder.buildService(IToiletService.class);

                Call<Integer> req = serv.addToilet(Toilet);
                try {
                    i = req.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return i;
            }
        });
        Thread t = new Thread(futureTask);
        t.start();
        Integer pId = null;
        try {
            pId = futureTask.get();
        } catch (Exception e) {
            Log.e("Thread error:", e.getMessage());
        }
        return pId;
    }

    public static void delToilet(int id)
    {
        FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                IToiletService serv = ServiceBuilder.buildService(IToiletService.class);

                Call<Void> req = serv.delToilet(id);
                try {
                    Response<Void> response = req.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        Thread t = new Thread(futureTask);
        t.start();

        try {
            futureTask.get();
        } catch (Exception e) {
            Log.e("Thread error:", e.getMessage());
        }
    }

    public static void updateToilet(Toilet Toilet)
    {
        FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                IToiletService serv = ServiceBuilder.buildService(IToiletService.class);

                Call<Void> req = serv.updateToilet(Toilet);
                try {
                    Response<Void> response = req.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        Thread t = new Thread(futureTask);
        t.start();

        try {
            futureTask.get();
        } catch (Exception e) {
            Log.e("Thread error:", e.getMessage());
        }
    }
}