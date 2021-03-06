package blockchain.scheduler;

import blockchain.scheduler.PSO.PSOScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.ArrayList;
import java.util.List;

public class PSOSchedule extends Schedule
{
    public PSOSchedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        super(tasks, machines);
        this.prepareSchedule();
    }

    public void prepareSchedule()
    {
        PSOScheduler scheduler = new PSOScheduler();
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
        System.out.println("PSO makespan: "+ maxFinishTime);
        this.makespan = maxFinishTime;
        this.flowtime = flowtime;
        this.calculateRestMetrics(list);
        this.calculatePfailure();
    }
}
