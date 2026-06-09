import { test, expect } from '@playwright/test';

test.describe('Authentication End-to-End Flow', () => {

  test('should register a new user, log out, and log back in successfully', async ({ page }) => {
    // Generate a unique email for every test run to prevent database collisions
    const uniqueEmail = `e2e_student_${Date.now()}@test.com`;
    const password = 'securepassword123';

    // 1. REGISTRATION PHASE
    await page.goto('/register');

    // Wait for the form to be visible
    await expect(page.getByRole('heading', { name: '註冊' })).toBeVisible();

    // Fill the registration form
    await page.locator('input[placeholder="信箱"]').fill(uniqueEmail);
    await page.locator('input[placeholder="至少6個字元"]').fill(password);
    await page.locator('input[placeholder="再次輸入密碼"]').fill(password);

    // Submit registration
    await page.getByRole('button', { name: '註冊' }).click();

    // Assert successful redirection to the student dashboard
    await expect(page).toHaveURL(/\/student/);

    // 2. LOGOUT PHASE
    // Assuming there is a logout button in the navigation bar when logged in
    // Note: If your app uses a dropdown for logout, you might need to click the dropdown first.
    // For now, we will just directly navigate to login to simulate a fresh session, 
    // or clear local storage to force logout.
    await page.evaluate(() => localStorage.clear());

    // 3. LOGIN PHASE
    await page.goto('/login');

    // Wait for login form
    await expect(page.getByRole('heading', { name: '登入' })).toBeVisible();

    // Fill login form
    await page.locator('input[placeholder="信箱"]').fill(uniqueEmail);
    await page.locator('input[type="password"]').fill(password);

    // Submit login
    await page.getByRole('button', { name: '登入' }).click();

    // Assert successful redirection back to student dashboard
    await expect(page).toHaveURL(/\/student/);
  });

});
