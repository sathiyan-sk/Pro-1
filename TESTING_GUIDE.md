# HR Page Integration - Testing Guide

## 🎯 Quick Verification Before Build

### Files Modified/Added:
1. ✅ `/app/src/main/resources/static/hrPage.html` - **NEW** (192KB)
2. ✅ `/app/src/main/resources/static/loginPage.html` - **MODIFIED**
3. ✅ `/app/src/main/java/com/trackerpro/controller/StaticContentController.java` - **MODIFIED**
4. ✅ `/app/src/main/java/com/trackerpro/config/SecurityConfig.java` - **MODIFIED**

---

## 🏗️ Build & Run Instructions

### Option A: Using Maven (if installed)
```bash
cd /app
mvn clean package -DskipTests
mvn spring-boot:run
```

### Option B: Using Java directly (if JAR exists)
```bash
cd /app
java -jar target/tracker-pro-spts-0.0.1-SNAPSHOT.jar
```

### Option C: Build with Maven Wrapper (if exists)
```bash
cd /app
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

**Expected Output:**
```
... Started TrackerProApplication in X seconds
... Tomcat started on port(s): 8080 (http)
```

---

## 🧪 Step-by-Step Testing

### Test 1: Verify Application Starts
```bash
curl http://localhost:8080/
```
**Expected:** HTML response (index page)

---

### Test 2: Access Login Page
```bash
curl http://localhost:8080/login
```
**Expected:** Login page HTML

**Browser Test:**
1. Open: `http://localhost:8080/login`
2. Verify: Login form displays correctly

---

### Test 3: HR Login
**Credentials:**
- Email: `mike.hr@tracker.com`
- Password: `hr123456`

**Steps:**
1. Open: `http://localhost:8080/login`
2. Enter email: `mike.hr@tracker.com`
3. Enter password: `hr123456`
4. Click "Login" button

**Expected Result:**
- ✅ Browser redirects to: `http://localhost:8080/hr`
- ✅ HR Dashboard loads
- ✅ Page title shows: "TrackerPro-HR"
- ✅ Sidebar visible with navigation
- ✅ User profile shows "Recruiter" / "HR Manager"

**If Redirect Fails:**
- Check browser console for errors
- Check backend logs for authentication errors
- Verify UserService returns `userType: 'HR'`

---

### Test 4: HR Dashboard Features

#### 4.1 Navigation Test
Click through all menu items:
- ✅ **Applications** → Should show 3 sample candidates
  - Raj Sekar (APID001)
  - Priya Sharma (APID002)
  - Arjun Reddy (APID003)

- ✅ **Shortlisted** → Should show empty state with 3 tabs:
  - All Applications
  - Applied
  - PMIS

- ✅ **Interviews** → Should show empty state with 2 tabs:
  - Upcoming
  - Past

- ✅ **Hired** → Should show empty state

#### 4.2 Applications Page Features
- ✅ Search box works (filter by name/email)
- ✅ Date filter dropdown displays
- ✅ Checkboxes select candidates
- ✅ "Select All" checkbox works
- ✅ Bulk action bar appears when items selected
- ✅ Status badges display (New, Under Review, etc.)
- ✅ Action buttons work:
  - "Under Review"
  - "Shortlist"
  - "Reject"
- ✅ "View Resume" button exists

#### 4.3 Visual Elements
- ✅ Sidebar navigation collapses/expands
- ✅ Menu toggle button (hamburger icon) works
- ✅ Accordion sections expand/collapse
- ✅ Cards display properly
- ✅ Responsive design works on mobile

---

### Test 5: Logout Functionality
**Steps:**
1. Click user profile icon (top-right)
2. Dropdown menu appears
3. Click "Logout"

**Expected:**
- ✅ Redirects to: `http://localhost:8080/login`
- ✅ Cannot access `/hr` without logging in again
- ✅ SessionStorage cleared

**Verification:**
```bash
# After logout, try to access HR page directly
curl -I http://localhost:8080/hr
```
Should still return 200 (page exists) but browser should redirect on client-side

---

### Test 6: Other User Types (Verify No Impact)

#### Admin Login
**Credentials:**
- Email: `admin@tracker.com`
- Password: `admin123`

**Expected:**
- ✅ Redirects to: `http://localhost:8080/admin`
- ✅ Admin page loads (unchanged)

#### Faculty Login
**Credentials:**
- Email: `sarah.faculty@tracker.com`
- Password: `faculty123`

**Expected:**
- ✅ Redirects to: `http://localhost:8080/admin`
- ✅ Admin page loads (unchanged)

#### Student Login (if exists)
**Expected:**
- ✅ Redirects to: `http://localhost:8080/student`
- ✅ Student application page loads (unchanged)

---

## 🐛 Troubleshooting

### Issue: HR login redirects to admin page instead of /hr
**Solution:** 
- Check if `loginPage.html` was properly saved
- Verify browser cache (hard refresh: Ctrl+Shift+R)
- Check browser console for JavaScript errors

### Issue: /hr returns 404 Not Found
**Solution:**
- Verify `StaticContentController.java` has `/hr` mapping
- Rebuild application: `mvn clean package`
- Check if `hrPage.html` exists in `src/main/resources/static/`

### Issue: /hr returns 403 Forbidden
**Solution:**
- Check `SecurityConfig.java` permits `/hr` route
- Verify line 59-84 includes `/hr` in `.requestMatchers()`

### Issue: HR page loads but shows blank
**Solution:**
- Check browser console for JavaScript errors
- Verify `hrPage.html` is complete (192KB, 3324 lines)
- Check if CSS/JavaScript is properly embedded

### Issue: Logout doesn't work
**Solution:**
- Check browser console for errors
- Verify logout script is present (lines 3244-3258 in hrPage.html)
- Clear browser cache

---

## ✅ Success Criteria

Your integration is successful if:

1. ✅ HR user can login with `mike.hr@tracker.com` / `hr123456`
2. ✅ After login, browser shows `http://localhost:8080/hr`
3. ✅ HR Dashboard displays with sidebar navigation
4. ✅ Applications page shows 3 sample candidates
5. ✅ All navigation menu items are clickable
6. ✅ Search and filter controls are visible
7. ✅ Logout button redirects to login page
8. ✅ Other user types (admin, faculty, student) still work correctly

---

## 📊 Test Report Template

```
HR Page Integration Test Report
================================

Date: _____________
Tester: ___________
Environment: Local / Dev / Prod

Test Results:
[ ] Application builds successfully
[ ] Application starts without errors
[ ] Login page loads
[ ] HR login successful
[ ] HR dashboard loads
[ ] Applications page shows 3 candidates
[ ] Navigation works (all 4 pages)
[ ] Search functionality works
[ ] Checkboxes and bulk actions work
[ ] Logout functionality works
[ ] Admin login still works
[ ] Faculty login still works
[ ] Student features unchanged

Issues Found:
_________________________________
_________________________________

Overall Status: PASS / FAIL

Notes:
_________________________________
_________________________________
```

---

## 🔄 Rollback Plan (If Needed)

If you encounter critical issues:

### Git Rollback
```bash
cd /app
git log --oneline -10  # Find last good commit
git checkout <commit-hash> -- src/main/resources/static/loginPage.html
git checkout <commit-hash> -- src/main/java/com/trackerpro/controller/StaticContentController.java
git checkout <commit-hash> -- src/main/java/com/trackerpro/config/SecurityConfig.java
rm src/main/resources/static/hrPage.html
mvn clean package
```

### Manual Rollback
1. Delete `hrPage.html`
2. Revert `loginPage.html` changes (change `/hr` back to `/admin` for HR)
3. Remove `/hr` route from `StaticContentController.java`
4. Remove `/hr` from `SecurityConfig.java`
5. Rebuild

---

**Document Version:** 1.0  
**Last Updated:** November 11, 2025  
**Status:** Ready for Testing
