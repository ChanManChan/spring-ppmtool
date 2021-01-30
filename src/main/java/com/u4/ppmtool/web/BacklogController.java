package com.u4.ppmtool.web;

import com.u4.ppmtool.domain.ProjectTask;
import com.u4.ppmtool.services.MapValidationError;
import com.u4.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationError mapValidationError;

    @PostMapping("/{backlogId}")
    public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result, @PathVariable String backlogId, Principal principal) {
        ResponseEntity<?> errorMap = mapValidationError.MapValidationService(result);

        if(errorMap != null) return errorMap;

        ProjectTask createdProjectTask = projectTaskService.addProjectTask(backlogId, projectTask, principal.getName());

        return new ResponseEntity<ProjectTask>(createdProjectTask, HttpStatus.CREATED);
    }

    @GetMapping("/{backlogId}")
    public ResponseEntity<List<ProjectTask>> getBacklogTasks(@PathVariable String backlogId, Principal principal) {
        return new ResponseEntity<List<ProjectTask>>(projectTaskService.fetchBacklogTasks(backlogId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> getProjectTask(@PathVariable String backlogId, @PathVariable String ptId, Principal principal) {
        ProjectTask projectTask = projectTaskService.findPTByProjectSequence(backlogId, ptId, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> updateProjectTask(@PathVariable String backlogId, @PathVariable String ptId, @Valid @RequestBody ProjectTask projectTask, BindingResult result, Principal principal) {
        ResponseEntity<?> errorMap = mapValidationError.MapValidationService(result);

        if(errorMap != null) return errorMap;

        return new ResponseEntity<ProjectTask>(projectTaskService.updateByProjectSequence(backlogId, ptId, projectTask, principal.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String backlogId, @PathVariable String ptId, Principal principal) {
        projectTaskService.deletePTByProjectSequence(backlogId, ptId, principal.getName());

        return new ResponseEntity<String>("Project Task '" + ptId + "' was deleted successfully", HttpStatus.OK);
    }
}
