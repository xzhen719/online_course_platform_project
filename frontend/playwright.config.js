import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  //look for test files in ./tests
  testDir: './tests',

  //Runs the test files at the same time
  fullyParallel: true,

  //If this is true, it will stop running the tests if it finds a test that fails
  forbidOnly: !!process.env.CI,

  //If on GitHub action, it will retry the test twice, If local, 0 times.
  retries: process.env.CI ? 2 : 0,

  //GitHub action, 1 worker; local as many as can handle.
  workers: process.env.CI ? 1 : undefined,

  //Generates a HTML report
  reporter: 'html',

  //set up baseURL and trace
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],
  // Start the Vite dev server locally if it's not already running.
  // If docker-compose is running (exposing 5173), this will just reuse that connection
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: true,
  },
});
