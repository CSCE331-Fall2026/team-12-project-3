# Sprint Planning Meeting Minutes

* **Date:** November 9, 2026
* **Time:** 11:00 PM - 11:30 PM
* **Attendees:** Kedar Panchal, Evan Chang, Vihal Arumalla

---

## Meeting Summary

During this session, we reevaluated our planned tickets for sprint 2, running a game of planning poker to assign story points to sprint 2 tasks.

### Key Highlights
* **User Story Creation:** Created user stories for sprint 2 tasks including backend deployment, novel feature prototyping, and ordering interface.
* **Sprint Planning Reevaluation:** Reflected on over-assiging of story points in sprint 1 to create more realistic point allocations in sprint 2.

# Scrum Meeting 1

* **Date:** November 13, 2026
* **Time:** 11:50 AM – 12:20 PM
* **Attendees:** Kedar Panchal, Vihal Arumalla

## Meeting Summary

During this session, we discussed connecting the frontend to the backend deployment URL once created and how to more securely handle CORS issues.

### Key Highlights
* **Progress Made:** The backend is containerized and a Render project has been created. Environment variables still need to be set up in the project, however.
* **Next Steps:** We plan on editing the CORS settings in the backend to allow for requests from the frontend deployment URL.
* **Blockers:** Evan, who wasn't present at the meeting, is the owner of the Google Cloud Project that contains our OAuth configuration. He needs to change the configuration settings to allow for the backend to interact with OAuth2.

# Scrum Meeting 2

* **Date:** November 18, 2026
* **Time:** 11:50 AM – 12:20 PM
* **Attendees:** Kedar Panchal, Evan Chang, Vihal Arumalla

## Meeting Summary

During this session, we discussed the predictive restocking API and how the frontend will interact with it.

### Key Highlights
**Progress Made:** The backend is fully up and running on Render, and OAuth2 is fully functional. The database interaction portion of the predictive restocking API is halfway complete.
**Next Steps:** We plan on finishing predictive utensil restocking and building out API endpoints for the full predictive restocking functionality. We also plan on adding UI elements in the existing restock flow to indicate when an ingredient or utensil should be restocked.
**Blockers:** Vihal needs the API to be finalized before he can finish the frontend implementation of the functionality.

# Scrum Meeting 3

* **Date:** November 21, 2026
* **Time:** 2:00 PM – 2:30 PM
* **Attendees:** Kedar Panchal, Evan Chang, Vihal Arumalla

## Meeting Summary

During this session we discussed some issues we had with integrating the customer ordering interface with the backend and how to resolve them.

### Key Highlights
**Progress Made:** The predictive restocking API is fully functional and the frontend has been updated to reflect the new functionality. The customer ordering interface has been started but has no connection to the backend.
**Next Steps:** We plan on finishing the customer order interface and connecting it to the backend, as well as resolving any missing functionality in the backend (detailed in blockers).
**Blockers:** Placing orders in the backend requires a Java class that's not serializable by Jackson, which is preventing the frontend from communicating with the backend at all. We need to modify the backend to accept a serializable object and construct the non-serializable object from it.
