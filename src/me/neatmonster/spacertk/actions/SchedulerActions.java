/*
 * This file is part of SpaceRTK (http://spacebukkit.xereo.net/).
 *
 * SpaceRTK is free software: you can redistribute it and/or modify it under the terms of the
 * Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license as published by the Creative
 * Common organization, either version 3.0 of the license, or (at your option) any later version.
 *
 * SpaceRTK is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA)
 * license along with this program. If not, see <http://creativecommons.org/licenses/by-nc-sa/3.0/>.
 */
package me.neatmonster.spacertk.actions;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import me.neatmonster.spacemodule.api.Action;
import me.neatmonster.spacemodule.api.UnhandledActionException;
import me.neatmonster.spacertk.scheduler.Job;
import me.neatmonster.spacertk.scheduler.Scheduler;
import me.neatmonster.spacertk.scheduler.UnSchedulableException;

/**
 * Actions handler for any Scheduler-related actions
 */
public class SchedulerActions {

    /**
     * Adds a job
     * @param jobName Name of the job
     * @param actionName Action to execute when the job is run
     * @param actionArguments Arguments to run the action with
     * @param timeType Type of time to schedule at
     * @param timeArgument Amount of time to schedule
     * @return If successful
     */
    @Action(
            aliases = {"addJob"})
    public boolean addJob(final String jobName, final String actionName, final Object[] actionArguments,
            final String timeType, final String timeArgument) {
        if (!Scheduler.getJobs().containsKey(jobName)) {
            Job job = null;
            try {
                job = new Job(actionName, actionArguments, timeType, timeArgument, false);
                Scheduler.addJob(jobName, job);
                return true;
            } catch (final UnSchedulableException e) {
                e.printStackTrace();
            } catch (final UnhandledActionException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    /**
     * Gets a list of all the jobs scheduled
     * @return All jobs currently scheduled
     */
    @Action(
            aliases = {"getJobs"})
    public LinkedHashMap<String, LinkedList<Object>> getJobs() {
        final LinkedHashMap<String, LinkedList<Object>> jobs = new LinkedHashMap<String, LinkedList<Object>>();
        for (final String jobName : Scheduler.getJobs().keySet()) {
            final Job job = Scheduler.getJobs().get(jobName);
            final LinkedList<Object> jobProperties = new LinkedList<Object>();
            jobProperties.add(job.actionName);
            jobProperties.add(Arrays.asList(job.actionArguments));
            jobProperties.add(job.timeType);
            jobProperties.add(job.timeArgument);
            jobs.put(jobName, jobProperties);
        }
        return jobs;
    }

    /**
     * Removes a job
     * @param jobName Job to be removed
     * @return If successful
     */
    @Action(
            aliases = {"removeJob"})
    public boolean removeJob(final String jobName) {
        Scheduler.removeJob(jobName);
        return true;
    }

    /**
     * Runs a job
     * @param jobName Job to run
     * @return If successful
     */
    @Action(
            aliases = {"runJob"})
    public Object runJob(final String jobName) {
        if (Scheduler.getJobs().containsKey(jobName)) {
            Scheduler.getJobs().get(jobName).run();
            return true;
        } else
            return false;
    }
}
