# Automated Testing Techniques Project

This project explores and compares advanced testing techniques applied to two case studies:

1. **DateHelper Class**: A utility class for date formatting and manipulation
2. **Quadratic Class**: A solver for quadratic equations with support for both real and complex roots

## Implemented Testing Techniques

Two main testing techniques were implemented:

### 1. Category-Partition Testing

Category-partition testing involves dividing the input domain into categories (or partitions) and testing representative values from each partition. This approach ensures systematic testing of different input scenarios.

Key implementations:
- `DateHelperCategoryPartitionTest.java`: Tests date formatting, conversions, and calculations with various date formats and edge cases
- `QuadraticCategoryPartitionTest.java`: Tests quadratic equation solver with different coefficient combinations representing positive, negative, and zero discriminants

### 2. Metamorphic Testing

Metamorphic testing focuses on relations between multiple executions of the program. It's particularly valuable when a direct "test oracle" is difficult to define.

Key implementations:
- `DateHelperMetamorphicTest.java`: Tests date transformations through relationships like format conversion symmetry and date calculation consistency
- `QuadraticMetamorphicTest.java`: Tests relations between quadratic equations such as scaling, reciprocals, and root properties

## Test Coverage Analysis

JaCoCo was used to analyze code coverage, focusing on branch coverage:

### DateHelper Coverage Results
- Over 75% of methods were covered
- Key functionality like date parsing, formatting, and calculations well covered
- Some date conversion and calculation methods with rare use cases remain untested

### Quadratic Coverage Results
- High branch coverage for the main solving algorithm (>90%)
- Complex root handling and precision management well tested
- Main user interface code (main method) has moderate coverage

## Mutation Testing Results

PIT (Pitest) was used to perform mutation testing:

### DateHelper Mutation Results
- Achieved ~60% mutation score
- Strong handling of core date conversion operations
- Opportunities for improvement in error handling paths

### Quadratic Mutation Results  
- Over 80% of mutants killed
- Strong performance with mathematical operations and root calculations
- Some survived mutants in boundary condition handling

## Performance Analysis

### Execution Time
- **Category-Partition Tests**: Average 0-10ms per test case
- **Metamorphic Tests**: Average 1-5ms per test case 
- More complex tests (e.g., date conversions across formats, quadratic roots with complex numbers) took longer

## Comparison of Techniques

### Category-Partition Testing
- **Strengths**: Systematic coverage of input domain, clear identification of edge cases
- **Weaknesses**: Requires explicit assertions, may miss interaction defects

### Metamorphic Testing
- **Strengths**: Excellent for properties that should hold across different executions, good at finding subtle mathematical bugs
- **Weaknesses**: More complex to design, requires understanding of system properties

## Coverage and Fault Detection Gaps

- Some error paths in the DateHelper class still lack coverage
- Complex interactions between multiple format conversions need more testing
- Additional boundary cases for the quadratic solver around very small coefficients would improve robustness

## Conclusion

For the given case studies:
- **Metamorphic testing** was particularly effective for the Quadratic solver, finding subtle mathematical relationship bugs
- **Category-partition testing** provided better systematic coverage of the DateHelper functionality
- Both techniques complemented each other, creating a robust test suite when used together

Together, these techniques achieved high code coverage, good mutation scores, and reasonable execution times, demonstrating their effectiveness for systematic software testing.

## Future Work

- Further improvement of test cases to achieve >90% branch coverage
- Additional metamorphic relations for date handling
- Performance testing for large-scale date operations
