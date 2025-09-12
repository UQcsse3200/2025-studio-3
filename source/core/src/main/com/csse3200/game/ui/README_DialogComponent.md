# Dialog Component

A flexible dialog component system for displaying warning, error, and info messages throughout the game.

## Features

- **Three Dialog Types**: INFO, WARNING, and ERROR with appropriate styling
- **Flexible Positioning**: Center by default, or specify custom coordinates
- **Callback System**: Handle user interactions (confirm, cancel, close)
- **Animations**: Smooth fade in/out transitions
- **Auto-dismiss**: Optional automatic dismissal after a specified delay
- **Easy Integration**: Simple API for quick dialog creation

## Quick Start

### Service Registration

First, register the DialogService in your screen initialization:

```java
// In your screen constructor or createUI method
ServiceLocator.registerDialogService(new DialogService());
```

### Using DialogManager (Recommended)

```java
// Simple info dialog
DialogManager.showInfoDialog("Information", "This is an info message");

// Warning dialog with callbacks
DialogManager.showWarningDialog(
    "Warning", 
    "This action may have consequences.",
    dialog -> System.out.println("User confirmed"),
    dialog -> System.out.println("User cancelled")
);

// Error dialog
DialogManager.showErrorDialog("Error", "Something went wrong!");

// Auto-dismiss dialog
DialogManager.showAutoDismissDialog(
    DialogType.INFO, 
    "Notice", 
    "This will disappear in 3 seconds",
    3f
);
```

### Using DialogService Directly

```java
// Get the service instance
DialogService dialogService = ServiceLocator.getDialogService();

// Use service methods directly
dialogService.showInfoDialog("Information", "This is an info message");
```

### Manual Creation

```java
// Create entity and dialog component
Entity dialogEntity = new Entity();
DialogComponent dialog = new DialogComponent(
    DialogType.WARNING, 
    "Custom Dialog", 
    "Custom message"
);

// Set up callbacks
dialog.setOnConfirm(d -> {
    System.out.println("Confirmed");
    dialog.hide();
});

dialog.setOnCancel(d -> {
    System.out.println("Cancelled");
    dialog.hide();
});

// Add to entity and register
dialogEntity.addComponent(dialog);
ServiceLocator.getEntityService().register(dialogEntity);

// Show the dialog
dialog.show();
```

## Dialog Types

### INFO
- **Color**: Blue
- **Buttons**: OK button only
- **Use Case**: Neutral information, notifications

### WARNING
- **Color**: Orange
- **Buttons**: Cancel and Continue buttons
- **Use Case**: Potential issues, confirmations

### ERROR
- **Color**: Red
- **Buttons**: OK button only
- **Use Case**: Error messages, critical issues

## API Reference

### DialogComponent

#### Constructor
```java
DialogComponent(DialogType dialogType, String title, String message)
```

#### Key Methods
- `show()` - Display the dialog with fade-in animation
- `hide()` - Hide the dialog with fade-out animation
- `setPosition(float x, float y)` - Set custom position
- `setSize(float width, float height)` - Set custom size
- `setAutoDismiss(boolean autoDismiss)` - Enable/disable auto-dismiss
- `setAutoDismissDelay(float delay)` - Set auto-dismiss delay in seconds
- `setOnConfirm(Consumer<DialogComponent> callback)` - Set confirm callback
- `setOnCancel(Consumer<DialogComponent> callback)` - Set cancel callback
- `setOnClose(Consumer<DialogComponent> callback)` - Set close callback

### DialogManager

#### Static Methods
- `showInfoDialog(String title, String message)` - Show info dialog
- `showWarningDialog(String title, String message, onConfirm, onCancel)` - Show warning dialog
- `showErrorDialog(String title, String message)` - Show error dialog
- `showConfirmationDialog(String title, String message, onConfirm, onCancel)` - Show confirmation dialog
- `showAutoDismissDialog(DialogType type, String title, String message, float delay)` - Show auto-dismiss dialog
- `showDialogAt(DialogType type, String title, String message, float x, float y)` - Show dialog at position
- `hideAllDialogs()` - Hide all active dialogs
- `getActiveDialogCount()` - Get number of active dialogs
- `hasActiveDialogs()` - Check if any dialogs are active

### DialogService

#### Instance Methods
- `showInfoDialog(String title, String message)` - Show info dialog
- `showWarningDialog(String title, String message, onConfirm, onCancel)` - Show warning dialog
- `showErrorDialog(String title, String message)` - Show error dialog
- `showConfirmationDialog(String title, String message, onConfirm, onCancel)` - Show confirmation dialog
- `showAutoDismissDialog(DialogType type, String title, String message, float delay)` - Show auto-dismiss dialog
- `showDialogAt(DialogType type, String title, String message, float x, float y)` - Show dialog at position
- `hideAllDialogs()` - Hide all active dialogs
- `getActiveDialogCount()` - Get number of active dialogs
- `hasActiveDialogs()` - Check if any dialogs are active
- `getActiveDialogs()` - Get list of active dialog components

## Examples

See `DialogExample.java` for comprehensive usage examples including:
- Basic dialog creation
- Callback handling
- Auto-dismiss functionality
- Custom positioning
- Manual dialog management

## Testing

Run the test suite with:
```bash
./gradlew test --tests DialogComponentTest
```

## Integration Notes

- **Service Registration**: Register DialogService in screen initialization
- Dialogs automatically center on screen by default
- High z-index (100) ensures dialogs appear above other UI elements
- Modal dialogs block interaction with other UI elements
- Automatic cleanup when dialogs are closed
- Compatible with the existing UI component system
- Follows the service pattern used throughout the game

## Screen Integration Example

```java
public class MyGameScreen extends ScreenAdapter {
  @Override
  public void show() {
    // Register services
    ServiceLocator.registerDialogService(new DialogService());
    // ... other service registrations
  }
  
  @Override
  public void dispose() {
    // Clean up services
    ServiceLocator.clear();
  }
}
```
