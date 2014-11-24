package com.github.axdotl.jqassistant.plugins.liquibase.exception;

/**
 * This exception maybe thrown if a blank {@link String} was passed when scanning a refactoring. (occurs when handling refactorings of a rollback)
 * 
 * @author Axel Koehler
 */
public class BlankStringRefactoringException extends Exception {

    private static final long serialVersionUID = -5847616095399697631L;
}
