/* global steelseries */

/**
 * Directive use to start a highlight.js block.
 *
 */
angular.module('steelseries',[])
	.directive('gauge',function(){

		return {
			restrict:'ACE',
                        replace: true,
                        scope: {
                            gaugeValue: '@'
                          },
                        template: '<canvas  width="301" height="301"></canvas>',
			link: function($scope,$elm,$attrs){

                            var sections, areas, radialGauge;
                            
                            sections = [steelseries.Section(-25, 0, 'rgba(0, 0, 220, 0.3)'),
                                       steelseries.Section(0, 15, 'rgba(0, 220, 0, 0.3)'),
                                          steelseries.Section(15, 30, 'rgba(220, 220, 0, 0.3)')],
                            areas = [steelseries.Section(30, 50, 'rgba(220, 0, 0, 0.3)')],
                            
                            radialGauge = new steelseries.Radial($elm[0], {
                                minValue: -25,
                                maxValue: 50,
                                gaugeType: steelseries.GaugeType.TYPE4,
                                size: 201,
                                section: sections,
                                area: areas,
                                titleString: "Temperatur",
                                unitString: "°C",
                                threshold: 50,
                                lcdVisible: true                        });
  
                            radialGauge.setFrameDesign(steelseries.FrameDesign.GLOSSY_METAL);
                            radialGauge.setBackgroundColor(steelseries.BackgroundColor.BRUSHED_METAL);

                            radialGauge.setValueAnimated($scope.gaugeValue ? $scope.gaugeValue : 0);
                            $attrs.$observe('gaugeValue', function(value){
                                radialGauge.setValue(value ? value : 0);
                            });        
                                    
			}
		};
	})
        ;

     