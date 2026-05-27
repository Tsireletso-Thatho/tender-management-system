package dao.interfaces;

import model.EvaluationCommittee;
import java.util.List;

/**
 * Data Access Object interface for Evaluation Committee Member entity operations.
 * Provides methods for retrieving evaluator information.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface EvaluationCommitteeDAO {
    
    /**
     * Finds an evaluator by their unique ID.
     * 
     * @param evaluatorId the evaluator ID to search for
     * @return the EvaluationCommittee object, or null if not found
     */
    EvaluationCommittee findById(int evaluatorId);
    
    /**
     * Finds an evaluator by their associated user ID.
     * 
     * @param userId the user ID to search for
     * @return the EvaluationCommittee object, or null if not found
     */
    EvaluationCommittee findByUserId(int userId);
    
    /**
     * Finds an evaluator by their employee number.
     * 
     * @param employeeNumber the employee number
     * @return the EvaluationCommittee object, or null if not found
     */
    EvaluationCommittee findByEmployeeNumber(String employeeNumber);
    
    /**
     * Retrieves all evaluation committee members.
     * 
     * @return List of all EvaluationCommittee objects
     */
    List<EvaluationCommittee> findAll();
    
    /**
     * Retrieves all active evaluation committee members.
     * 
     * @return List of active EvaluationCommittee objects
     */
    List<EvaluationCommittee> findAllActive();
    
    /**
     * Counts the total number of evaluation committee members.
     * 
     * @return the total count
     */
    int countAll();
    
    /**
     * Counts the number of active evaluation committee members.
     * 
     * @return the count of active evaluators
     */
    int countActive();
}