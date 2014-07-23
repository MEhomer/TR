import org.neo4j.graphdb.RelationshipType;

public enum RelationType implements RelationshipType{
    POINT_TO_POINT, WORKOUT_TO_POINT, WORKOUTTYPE_TO_WORKOUT, USER_TO_WORKOUT, STARTNODE_TO_USER;
}