# Overview

Habitizer is a routine management app developed in Android Studio. Users can create routines, add tasks, and set target completion times. As they carry out a routine, they can mark tasks as completed, and the app records the time spent on each task. This allows users to track their progress, identify where excessive time is being spent, and make adjustments to improve their efficiency and stay on schedule.

# Key Features

- Create custom routines
- Set goal time for each routine
- Add, edit, and reorder tasks
- Check off tasks as you complete them
- Built-in timer that tracks per-task and total routine time

# Demo

https://github.com/user-attachments/assets/7c2e39fc-f3dc-4ec2-8f09-6378596da8eb

# Development Workflow

This project was developed by a team of six using an Agile methodology. Customer requirements were translated into user stories, which were then broken down into smaller, manageable development tasks. The team worked in three pair-programming groups, with tasks distributed across two milestones, each consisting of two week-long iterations.

Weekly meetings were held to maintain clear communication and address any problems. At the end of each milestone, the team conducted a review to evaluate successes, identify areas for improvement, and refine the development process for future iterations.

To maintain high quality code in the main branch, the team adopted Continuous Integration and peer review practices. Automated JUnit test suites were executed through GitHub Actions on each change, and all pull requests required approval from two reviewers before being merged.

# Installation

- Install Android Studio at https://developer.android.com/studio
- After launching Android Studio, go to Device Manager on the right sidebar and create a Medium Phone device using API 35.
- Clone this repository in the terminal:
```
git clone https://github.com/CSE-110-Winter-2025/habitizer-team-9.git
```
- Open the project folder on Android Studio.
- Launch the Medium Phone virtual device and hit Run at the top next to the dropdown that says app.

# Assignment Calculations:

Iteration 1 Velocity Calculations:

List of Issues and Durations: (Issue, Expected Duration, Actual Duration):

1.1, 1, 1.5
1.2, 2, 2.5
1.3, 4, 7
1.4, 2, 2
1.5 (SSB), 1, 1
2.1, 2, 3
2.2, 2, 3
2.3, 2, 2
2.4, 1, 1.5
2.5, 1, 1.5
2.6 (SSB), 1
DevStory 1.1, 1, 2
DevStory 1.2, 1, 1

Velocity Calculation:  Expected Duration / Actual Duration = 21 / (1.5 + 2.5 + 7 + 2 + 1 + 3 + 3 + 2 + 1.5 + 1.5 + 1 + 2 + 1) = 21 / 29 = 0.724

Velocity of 0.724, applied to 5 * 6 = 30 working hours total. 30 * 0. 724 = 21.72 hours.

Number of estimated hours left for iteration 2: 20 hours.

Therefore, all tasks for MS 1 can be estimated to be completed by the end of I2.
