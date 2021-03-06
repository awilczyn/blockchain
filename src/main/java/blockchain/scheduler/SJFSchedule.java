package blockchain.scheduler;

import blockchain.scheduler.SJF.SJFScheduler;
import blockchain.scheduler.utils.Constants;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.*;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class SJFSchedule extends Schedule
{
    public SJFSchedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        super(tasks, machines);
        this.prepareSchedule();
    }

    public void prepareSchedule()
    {
        SJFScheduler scheduler = new SJFScheduler();
        List<Cloudlet> list = scheduler.schedule();
        Cloudlet cloudlet;
        Machine machine;
        double maxFinishTime = 0;
        double flowtime = 0;
        for (int i = 0; i < list.size(); i++) {
            cloudlet = list.get(i);
            machine = this.machines.get(cloudlet.getVmId());
            machine.addTaskToExecute(cloudlet.getCloudletId()+1);
            maxFinishTime = Math.max(maxFinishTime, cloudlet.getFinishTime());
            flowtime = flowtime + cloudlet.getFinishTime();
        }
        System.out.println("SJF makespan: "+ maxFinishTime);
        this.makespan = maxFinishTime;
        this.flowtime = flowtime;
        this.calculateRestMetrics(list);
        this.calculatePfailure();
    }
}
