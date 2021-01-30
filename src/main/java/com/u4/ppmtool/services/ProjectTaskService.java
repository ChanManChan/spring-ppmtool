package com.u4.ppmtool.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.u4.ppmtool.domain.Backlog;
import com.u4.ppmtool.domain.Project;
import com.u4.ppmtool.domain.ProjectTask;
import com.u4.ppmtool.exceptions.ProjectNotFoundException;
import com.u4.ppmtool.exceptions.ProjectTaskNotFoundException;
import com.u4.ppmtool.repositories.BacklogRepository;
import com.u4.ppmtool.repositories.ProjectRepository;
import com.u4.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
        // PTs to be added to a specific project, project != null, BL exists
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

        if(backlog == null) {
            throw new ProjectNotFoundException("Project with ID '" + projectIdentifier + "' not found");
        }

        if(!backlog.getProject().getProjectLeader().equals(username)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }

        // set the BL to PT
        projectTask.setBacklog(backlog);

        // we want our project sequence to be like this: IDPRO-1, IDPRO-2, IDPRO-3, ...100
        Integer backlogSequence = backlog.getPTSequence();

        // Update the BL sequence
        backlogSequence++;

        // Update the BL
        backlog.setPTSequence(backlogSequence);

        // Add sequence to project task
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        // Initial priority when priority null
        if(projectTask.getPriority() == null) {
            // In the future we need projectTask.getPriority() == 0 to handle the form
            projectTask.setPriority(3);
        }

        // Initial status when status is null
        if(projectTask.getStatus() == null) {
            projectTask.setStatus("TODO");
        }

        return projectTaskRepository.save(projectTask);
    }

    public List<ProjectTask> fetchBacklogTasks(String backlogId, String username) {
        Project project = projectRepository.findByProjectIdentifier(backlogId);

        if(project == null) {
            throw new ProjectNotFoundException("Project with ID '" + backlogId + "' not found");
        }

        if(!project.getProjectLeader().equals(username)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);
    }

    public ProjectTask findPTByProjectSequence(String backlogId, String sequence, String username) {
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlogId);

        if(backlog == null) {
            throw new ProjectNotFoundException("Project with ID '" + backlogId + "' does not exist");
        }

        if(!backlog.getProject().getProjectLeader().equals(username)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(sequence);

        if(projectTask == null) {
            throw new ProjectTaskNotFoundException("Project Task with ID '" + sequence + "' does not exist");
        }

        if(!projectTask.getProjectIdentifier().equals(backlogId)) {
            throw new ProjectTaskNotFoundException("Project Task with ID '" + sequence + "' does not exist in project '" + backlogId + "'");
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(String backlogId, String sequence, ProjectTask updatedTask, String username) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> fields = mapper.convertValue(updatedTask, new TypeReference<Map<String, Object>>() {});

        ProjectTask projectTask = findPTByProjectSequence(backlogId, sequence, username);

        fields.remove("id");
        fields.remove("projectIdentifier");

        fields.forEach((k, v) -> {
            if(v != null) {
                Field field = ReflectionUtils.findField(ProjectTask.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, projectTask, v);
            }
        });

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlogId, String sequence, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlogId, sequence, username);

        //        Backlog backlog = projectTask.getBacklog();
        //        List<ProjectTask> projectTasks = backlog.getProjectTasks();
        //        projectTasks.remove(projectTask);
        //        backlogRepository.save(backlog);

        projectTaskRepository.delete(projectTask);
    }
}
