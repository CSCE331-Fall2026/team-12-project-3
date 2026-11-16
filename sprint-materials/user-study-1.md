# User Study 1
## Study Goals
-
## Questions Asked
### Likert-scale
1. It was easy to find the login button.
2. It was easy to understand which system I was logging into.
3. It was easy to understand which items needed restocking.
4. It was easy to restock an item.
5. It was easy to navigate to each section in the inventory management system.
6. It was easy to find an inventory item and check its quantity and restock threshold.
### Open-ended / interview
1. Did you feel like you had all of the information needed to monitor 
ingredient quantities? What was missing?
## Participant Results & Observations
<!-- one block per participant / team member -->
### Participant A — [Manager] (session run by: [Kedar Panchal])
- Likert ratings:
1. Strongly Agree
2. Strongly Agree
3. Agree
4. Disagree
5. Strongly Agree
6. Strongly Agree
- Notable quotes:
The participant found most of the user interface intuitive and easy to navigate.
However, they pointed out some potential points of confusion on the restocking
workflow. Notably, they found that the spacing of the restock quantity and 
status indicator made the status indicator not immediately clear. They also 
noted that adding a difference between the current quantity and restock 
threshold would make calculating the quantity to restock easier. They also 
suggested that it be made clearer that the restocking process is additive.
- Observed behavior (hesitations, errors, friction):
The main observed behavior was that the participant was directly comparing
the current quantity and restock threshold to determine how much to restock.
They didn't notice the status indicator at all until they did so by accident 
when attempting to scroll down. They discovered a bug where restocking a utensil
caused an error message to appear.

### Participant B — [Manager] (session run by: [Evan Chang])
- Likert ratings:
1. Strongly Agree
2. Strongly Agree
3. Strongly Agree
4. Agree
5. Strongly Agree
6. Strongly Agree
- Notable quotes:
The participant found the user interface intuitive and easy to navigate. Their
main issue was that the manager view didn't label that it was for managers after
login. They had a 16x10 screen, which resulted in empty bars on the sides of the
screen.
- Observed behavior (hesitations, errors, friction):
The participant was able to complete most tasks without issue. They were 
annoyed with the load times when switching between sections in the inventory.

### Participant C — [Manager] (session run by: [Vihal Arumalla])
- Likert ratings:
1. Strongly Agree
2. Strongly Agree
3. Strongly Agree
4. Agree
5. Strongly Agree
6. Strongly Agree
- Notable quotes:
The participant successfully navigated through the user interface and was able
to restock items without issue. They suggested that the difference between the 
current quantity and restock threshold be made visible, and the fact that the
restocking process is additive be made clearer.
- Observed behavior (hesitations, errors, friction):
The participant was able to complete all tasks, but they were calculating the
difference between the current quantity and restock threshold to determine how 
much to restock. 
## Proposed Design Moves (prioritized)
| Priority | Proposed change | Evidence / justification | Sprint impact |
|----------|-----------------|--------------------------|---------------|
| 1 | Fix the utensil restocking bug. | Participant A encountered a bug when restocking a utensil, which caused an error message to appear. | Low (one-line change) |
| 2 | Make the UI work on different screen sizes. | Participant B had a 16x10 screen, which resulted in empty bars on the sides of the screen. | Medium (UI redesign) |
| 3 | Make the difference between the current quantity and restock threshold visible. | Participants A and C were calculating the difference between the current quantity and restock threshold to determine how much to restock. | Low (small UI change) |
| 4 | Make it clearer that the restocking process is additive. | Participants A and C suggested that it be made clearer that the restocking process is additive. | Low (small UI change) |
