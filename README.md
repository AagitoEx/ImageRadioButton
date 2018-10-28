# ImageRadioButton
Android Custom Radio Button

# Step 1: Configure gradle
Include the project in gradle:
```
dependencies {
    implementation 'com.github.aagitoEx:imageradiobutton:1.0'
}
```

# Step 2: Use in XML Layout
`RadioImageGroup` is the container layout for `RadioImageButton`. All `RadioImageButton` must me inside `RadioImageGroup` or else will not work as expected.
```
<com.aagito.imageradiobutton.RadioImageGroup
    android:id="@+id/radioImageGroup"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:presetRadioCheckedId="@id/female">

    <com.aagito.imageradiobutton.RadioImageButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="8dp"
        app:drawableIcon="@drawable/ic_bluetooth_black_24dp"
        app:iconPosition="left"
        app:iconSize="48dp"
        app:selectedBackgroundColor="#ddFFdd"
        app:selectedIconColor="@color/colorPrimary"
        app:text="Male" />

    <com.aagito.imageradiobutton.RadioImageButton
        android:id="@+id/female"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="8dp"
        app:drawableIcon="@drawable/ic_bluetooth_black_24dp"
        app:iconPosition="right"
        app:iconSize="48dp"
        app:selectedBackgroundColor="#ddFFdd"
        app:selectedIconColor="@color/colorPrimary"
        app:text="Female" />

</com.aagito.imageradiobutton.RadioImageGroup>
```
*** Custom Properties ***
Available Attributes for `RadioImageGroup`:
```
<declare-styleable name="RadioImageGroup">
    <attr name="presetRadioCheckedId" format="reference" />
</declare-styleable>
```

Available Attributes for `RadioImageButton`:
```
<declare-styleable name="RadioImageButton">
    <attr name="text" format="string" />
    <attr name="drawableIcon" format="reference" />
    <attr name="textColor" format="color" />
    <attr name="selectedTextColor" format="color" />
    <attr name="iconColor" format="color" />
    <attr name="selectedIconColor" format="color" />
    <attr name="textSize" format="dimension" />
    <attr name="iconSize" format="dimension"/>
    <attr name="backgroundColor" format="color"/>
    <attr name="selectedBackgroundColor" format="color"/>
    <attr name="iconPosition">
        <flag name="top" value="0"/>
        <flag name="right" value="1"/>
        <flag name="bottom" value="2"/>
        <flag name="left" value="3"/>
    </attr>
</declare-styleable>
```