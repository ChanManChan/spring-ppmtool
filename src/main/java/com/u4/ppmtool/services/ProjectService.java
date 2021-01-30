package com.u4.ppmtool.services;

import com.u4.ppmtool.domain.Backlog;
import com.u4.ppmtool.domain.Project;
import com.u4.ppmtool.domain.User;
import com.u4.ppmtool.exceptions.ProjectIdException;
import com.u4.ppmtool.exceptions.ProjectNotFoundException;
import com.u4.ppmtool.repositories.ProjectRepository;
import com.u4.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveProject(Project project, String username) {
        try {
            User user = userRepository.findByUsername(username);

            project.setUser(user);
            project.setProjectLeader(user.getUsername());
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

    public Project findProjectByIdentifier(String projectId, String username) {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null) {
            throw new ProjectIdException("Project ID '" + projectId + "' does not exist");
        }

        if(!project.getProjectLeader().equals(username)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return project;
    }

    public Iterable<Project> findAllProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username) {
        projectRepository.delete(findProjectByIdentifier(projectId, username));
    }

    public Project updateProjectByIdentifier(String projectId, Project project, String username) {
        Project queriedProject = findProjectByIdentifier(projectId, username);

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
