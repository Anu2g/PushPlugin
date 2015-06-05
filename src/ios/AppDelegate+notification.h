//
//  AppDelegate+notification.h
//  pushtest
//
//  Created by Robert Easterday on 10/26/12.
//
//

#import "AppDelegate.h"

@interface AppDelegate (notification)
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
- (void)applicationDidBecomeActive:(UIApplication *)application;
- (id) getCommandInstance:(NSString*)className;

- (void)applicationPushPlugin:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo;
- (void)applicationPushPlugin:(UIApplication *) application handleActionWithIdentifier: (NSString *) identifier
         forLocalNotification: (NSDictionary *) notification;

@property (nonatomic, retain) NSDictionary	*launchNotification;

@end
