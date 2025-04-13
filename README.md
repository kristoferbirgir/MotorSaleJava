# Software Project II - HBV601G - Team 22

Project in Software Project II course in HÍ, the project will be based on creating a Android app that allows individuals to advertise and sell cars on the web without going through 3rd parties, ie. car dealerships.


## Progress Report
### 16th February, 2025 - Sprint 1
- Class, state and sequence diagrams have been made.
  - Diagrams can be found here: https://app.diagrams.net/?title=Hugb%C3%BAna%C3%B0arverkefni%202%20-%20UML&client=1#G10NHfiOMietTVYVihU51Z4qpSbD6ubtqV#%7B%22pageId%22%3A%227yQUwnZzKYi_pSdOcJuR%22%7D
- Repo has been created.
- First user story (As a prospective seller I would like to be able to create an account so that I can list my car for sale and manage my listings easily) has been implemented.
- Product owner: Aser Kroma

### 9th March, 2025 - Sprint 2
- User stories 2-7 have been implemented.
- After discussion, the user stories were split up between the development team.
- These user stories include a mobile specific user story which allows the user to take a photo when creating a listing.
- Product owner: Janus Bjarki Birgisson

### 30th March, 2025 - Sprint 3
- User stories 8-12 have been iplemented.
- After dividing the stories among the development team, the work went rather smoothly with one exception.
- We encountered some difficulties with implementing the sort and filter functionality, as the backend did not support a single query for multiple filters. We had initially implemented the backend with a separate endpoint for each filter. Because of this, it took us longer than expected to find a way to make it work. If the user selects both sorting and filtering, all the filtering happens in the backend. The lists returned from the backend are then intersected, and the intersection is sorted in the app if the user selects a sorting mechanism.
- Product owner: Viktor Örlygur Andrason

###
- User stories 13–15 have been implemented. During this final push, we focused on polishing all functionalities to ensure a smooth user experience and reliable performance.
- The project is now stable and ready for presentation.
- Product owner: Kristófer Birgir
