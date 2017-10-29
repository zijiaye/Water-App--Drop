package gatech.cs2340.android.drop;

import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import gatech.cs2340.android.drop.controllers.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

/**
 * Testing class for Login Activity screen
 * Method Signature: private boolean validate()
 *
 * @author Jayden Sun
 */

@RunWith(AndroidJUnit4.class)
public class LoginValidateTest {
    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new ActivityTestRule<>(LoginActivity.class);


    @Test
    public void emailIsEmpty() {
        onView(withId(R.id.login_email_input)).perform(clearText());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_email_input)).check(matches(withError("Enter a valid email address")));
    }

    @Test
    public void emailIsInvalid() {
        onView(withId(R.id.login_email_input)).perform(typeText("1234567"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_email_input)).check(matches(withError("Enter a valid email address")));
    }

    @Test
    public void emailValid() {
        onView(withId(R.id.login_email_input)).perform(typeText("user@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(allOf(withId(R.id.login_email_input), isDisplayed())).check(matches(hasNoErrorText()));
    }

    @Test
    public void onlyEmail() {
        onView(withId(R.id.login_email_input)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_password_input)).check(matches(withError("Password is empty")));
    }

    @Test
    public void onlypassword() {
        onView(withId(R.id.login_password_input)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_email_input)).check(matches(withError("Enter a valid email address")));
    }

    @Test
    public void passwordIsEmpty() {
        onView(withId(R.id.login_email_input)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(clearText());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_password_input)).check(matches(withError("Password is empty")));
    }

    @Test
    public void validInfo() {
        onView(withId(R.id.login_email_input)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("user@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(allOf(withId(R.id.login_email_input), isDisplayed())).check(matches(hasNoErrorText()));
    }

    @Test
    public void loginFailed_shouldShowToast() {
        onView(withId(R.id.login_email_input)).perform(typeText("user@email.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText(getString(R.string.auth_failed)))
                .inRoot(withDecorView(
                        not(is(mLoginActivityTestRule.getActivity().
                                getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

/*   @Test
    public void loginSuccessfully() {
        onView(withId(R.id.login_email_input)).perform(typeText("test@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password_input)).perform(typeText("1234567"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
       SystemClock.sleep(1000);
        //intended(hasComponent(SourceReportActivity.class.getName()));
       //SystemClock.sleep(2000);
       onView(withId(R.id.ic_setting)).perform(click());
       //SystemClock.sleep(1500);
       //onView(withId(R.id.ic_setting)).check(matches(isClickable()));
       //onView(withId(R.id.ic_setting)).check(matches(isEnabled()));

   }*/

    private String getString(@StringRes int resourceId) {
        return mLoginActivityTestRule.getActivity().getString(resourceId);
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof EditText) {
                    return ((EditText) item).getError().toString().equals(expected);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not found error message" + expected + ", find it!");
            }
        };
    }

    private static Matcher<View> hasNoErrorText() {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has no error text: ");
            }

            @Override
            protected boolean matchesSafely(EditText view) {
                return view.getError() == null;
            }
        };
    }


}
