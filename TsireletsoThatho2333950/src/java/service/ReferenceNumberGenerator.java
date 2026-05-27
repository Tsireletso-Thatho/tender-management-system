package service;

import dao.implementations.SupplierDAOImpl;
import dao.implementations.TenderDAOImpl;
import util.Constants;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for generating unique reference numbers.
 * Handles generation of tender reference numbers and supplier registration numbers.
 * 
 * Required by Module 2: Reference number generation (MPW-YYYY-NNNN)
 * Required by Module 1: Supplier registration number generation (SUP-YYYY-NNNN)
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ReferenceNumberGenerator {
    
    private static final Logger LOGGER = Logger.getLogger(ReferenceNumberGenerator.class.getName());
    
    private final TenderDAOImpl tenderDAO;
    private final SupplierDAOImpl supplierDAO;
    
    /**
     * Constructor initializes DAO instances.
     */
    public ReferenceNumberGenerator() {
        this.tenderDAO = new TenderDAOImpl();
        this.supplierDAO = new SupplierDAOImpl();
    }
    
    /**
     * Generates a unique tender reference number.
     * Format: MPW-YYYY-NNNN where YYYY is current year and NNNN is sequential number.
     * 
     * @return the generated reference number
     */
    public String generateTenderReference() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int sequence = tenderDAO.getNextSequenceNumber();
        String reference = String.format("%s-%d-%04d", Constants.TENDER_REF_PREFIX, year, sequence);
        
        LOGGER.log(Level.FINE, "Generated tender reference: {0}", reference);
        return reference;
    }
    
    /**
     * Generates a unique supplier registration number.
     * Format: SUP-YYYY-NNNN where YYYY is current year and NNNN is sequential number.
     * 
     * @return the generated registration number
     */
    public String generateSupplierRegistrationNumber() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int sequence = supplierDAO.getNextSequenceNumber();
        String registration = String.format("%s-%d-%03d", Constants.SUPPLIER_REG_PREFIX, year, sequence);
        
        LOGGER.log(Level.FINE, "Generated supplier registration: {0}", registration);
        return registration;
    }
    
    /**
     * Generates a unique employee number for staff.
     * Format: MPW-OFF-NNN for officers, MPW-EVAL-NNN for evaluators.
     * 
     * @param prefix the prefix (OFF or EVAL)
     * @param sequence the sequential number
     * @return the generated employee number
     */
    public String generateEmployeeNumber(String prefix, int sequence) {
        return String.format("MPW-%s-%03d", prefix, sequence);
    }
    
    /**
     * Gets the next sequence number for tenders in the current year.
     * 
     * @return the next sequence number
     */
    public int getNextTenderSequence() {
        return tenderDAO.getNextSequenceNumber();
    }
    
    /**
     * Gets the next sequence number for suppliers in the current year.
     * 
     * @return the next sequence number
     */
    public int getNextSupplierSequence() {
        return supplierDAO.getNextSequenceNumber();
    }
    
    /**
     * Gets the current year for reference number generation.
     * 
     * @return the current year
     */
    public int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}