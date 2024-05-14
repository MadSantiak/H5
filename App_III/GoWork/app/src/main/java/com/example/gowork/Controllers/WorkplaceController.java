package com.example.gowork.Controllers;

import android.util.Log;
import com.example.gowork.Model.Workplace.IWorkplaceService;
import com.example.gowork.Model.Workplace.Workplace;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class WorkplaceController {
    public static Workplace getWorkplaceById (int id)
    {
        FutureTask<Workplace> futureTask = new FutureTask<>(new Callable<Workplace>() {
            @Override
            public Workplace call() {
                Workplace p = null;
                IWorkplaceService serv =
                        ServiceBuilder.buildService(IWorkplaceService.class);

                Call<Workplace> req = serv.getWorkplaceById(id);
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
        Workplace Workplace = null;
        try {
            Workplace = futureTask.get();
        } catch (Exception e) {
            Log.d("Thread", e.getMessage());
        }
        return Workplace;
    }
    public static List<Workplace> getAllWorkplace() {
        FutureTask<List<Workplace>> futureTask = new FutureTask<>(new Callable<List<Workplace>>() {
            @Override
            public List<Workplace> call() throws Exception {
                List<Workplace> people = null;
                IWorkplaceService serv = ServiceBuilder.buildService(IWorkplaceService.class);
                Call<List<Workplace>> request = serv.getAllWorkplace();
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
        List<Workplace> people = null;
        try {
            people = futureTask.get();
        } catch (Exception e) {
        }
        return people;
    }

    public static Integer addWorkplace(Workplace Workplace)
    {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer i = null;
                IWorkplaceService serv = ServiceBuilder.buildService(IWorkplaceService.class);

                Call<Integer> req = serv.addWorkplace(Workplace);
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
    public static void updateWorkplace(Workplace workplace)
    {
        FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                IWorkplaceService serv = ServiceBuilder.buildService(IWorkplaceService.class);

                Call<Void> req = serv.updateWorkplace(workplace);
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