package com.example.gowork.Controllers;

import android.util.Log;
import com.example.gowork.Model.Workperiod.IWorkperiodService;
import com.example.gowork.Model.Workperiod.Workperiod;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class WorkperiodController {
    public static Workperiod getWorkperiodById (int id)
    {
        FutureTask<Workperiod> futureTask = new FutureTask<>(new Callable<Workperiod>() {
            @Override
            public Workperiod call() {
                Workperiod p = null;
                IWorkperiodService serv =
                        ServiceBuilder.buildService(IWorkperiodService.class);

                Call<Workperiod> req = serv.getWorkperiodById(id);
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
        Workperiod Workperiod = null;
        try {
            Workperiod = futureTask.get();
        } catch (Exception e) {
            Log.d("Thread", e.getMessage());
        }
        return Workperiod;
    }
    public static List<Workperiod> getAllWorkperiod() {
        FutureTask<List<Workperiod>> futureTask = new FutureTask<>(new Callable<List<Workperiod>>() {
            @Override
            public List<Workperiod> call() throws Exception {
                List<Workperiod> workperiod = null;
                IWorkperiodService serv = ServiceBuilder.buildService(IWorkperiodService.class);
                Call<List<Workperiod>> request = serv.getAllWorkperiod();
                try {
                    workperiod = request.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return workperiod;
            }
        });
        Thread t = new Thread(futureTask);
        t.start();
        List<Workperiod> workperiod = null;
        try {
            workperiod = futureTask.get();
        } catch (Exception e) {
        }
        return workperiod;
    }

    public static Integer addWorkperiod(Workperiod Workperiod)
    {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer i = null;
                IWorkperiodService serv = ServiceBuilder.buildService(IWorkperiodService.class);

                Call<Integer> req = serv.addWorkperiod(Workperiod);
                Log.d("Test", req.toString());
                try {
                    i = req.execute().body();
                    Log.d("Test2", i.toString());
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

    public static void updateWorkperiod(Workperiod workperiod)
    {
        FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                IWorkperiodService serv = ServiceBuilder.buildService(IWorkperiodService.class);

                Call<Void> req = serv.updateWorkperiod(workperiod);
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
    public static void delWorkperiod(int id)
    {
        FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                IWorkperiodService serv = ServiceBuilder.buildService(IWorkperiodService.class);

                Call<Void> req = serv.delWorkperiod(id);
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