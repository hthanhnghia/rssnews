import React from 'react';
import {TouchableOpacity, Image, StyleSheet, Text} from 'react-native';

// withNavigation allows components to dispatch navigation actions
import {withNavigation} from 'react-navigation';

// DrawerActions is a specific type of navigation dispatcher
import {DrawerActions} from 'react-navigation-drawer';

class DrawerTrigger extends React.Component {
  render() {
    return (
      <TouchableOpacity
        style={styles.trigger}
        onPress={() => {
          this.props.navigation.dispatch(DrawerActions.openDrawer());
        }}>
        <Image
          style={styles.triggerImage}
          source={require('../assets/menu.png')}
        />
        <Text style={styles.headerTitle}>NEWS</Text>
      </TouchableOpacity>
    );
  }
}

const styles = StyleSheet.create({
  trigger: {
    marginLeft: 20,
    borderRadius: 30,
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
  },
  triggerImage: {
    width: 20,
    height: 20,
    marginRight: 20,
  },
  headerTitle: {
    color: 'white',
    fontSize: 20,
  },
});

export default withNavigation(DrawerTrigger);
