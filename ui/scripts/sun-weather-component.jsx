import React from 'react';

export default class SunWeatherComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            sunrise: null,
            sunset: null,
            temperature: null,
            requests: null
        };
    }

    render() {
        return (
            <div>
                <div>Sunrise time: {this.state.sunrise}</div>
                <div>Sunset time: {this.state.sunset}</div>
                <div>Temperature: {this.state.temperature}</div>
                <div>Requests: {this.state.requests}</div>
            </div>
        );
    }

}