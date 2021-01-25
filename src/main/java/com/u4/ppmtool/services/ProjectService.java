package com.u4.ppmtool.services;

import com.u4.ppmtool.domain.Backlog;
import com.u4.ppmtool.domain.Project;
import com.u4.ppmtool.exceptions.ProjectIdException;
import com.u4.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project) {
        try {
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId) {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null) {
            throw new ProjectIdException("Project ID '" + projectId + "' does not exist");
        }

        return project;
    }

    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId) {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null) {
            throw new ProjectIdException("Cannot delete project with ID '" + projectId + "'. This project does not exist");
        }

        projectRepository.delete(project);
    }

    public Project updateProjectByIdentifier(String projectId, Project project) {
        Project queriedProject = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(queriedProject == null) {
            throw new ProjectIdException("Cannot update project with ID '" + projectId + "'. This project does not exist");
        }

        if(project.getDescription() != null) {
            queriedProject.setDescription(project.getDescription());
        }
        if(project.getProjectName() != null) {
            queriedProject.setProjectName(project.getProjectName());
        }

        Project updatedProject = projectRepository.save(queriedProject);

        return updatedProject;
    }
}
